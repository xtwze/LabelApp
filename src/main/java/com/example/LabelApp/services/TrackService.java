package com.example.LabelApp.services;

import com.example.LabelApp.dto.AddTrackDto;
import com.example.LabelApp.dto.CommentDto;
import com.example.LabelApp.dto.ShowTrackDto;
import com.example.LabelApp.dto.TrackPreviewDto;
import com.example.LabelApp.models.*;
import com.example.LabelApp.models.enums.Role;
import com.example.LabelApp.repositories.TrackRepository;
import com.example.LabelApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    // Директории для хранения файлов
    private static final String UPLOAD_ROOT = "uploads";
    private static final String COVER_DIR = UPLOAD_ROOT + "/covers";
    private static final String AUDIO_DIR = UPLOAD_ROOT + "/audio";

    /**
     * Список превью треков для главной страницы
     */
    @Transactional(readOnly = true)
    public List<TrackPreviewDto> listTrackPreviews(String title) {
        List<Track> tracks = title != null && !title.isBlank()
                ? trackRepository.findByTitleContainingIgnoreCase(title.trim())
                : trackRepository.findAll();

        return tracks.stream()
                .map(this::toPreviewDto)
                .collect(Collectors.toList());
    }

    /**
     * Полная информация о треке по ID (с комментариями через DTO)
     */
    @Transactional(readOnly = true)
    public ShowTrackDto getShowTrackDtoById(Long id) {
        Track track = getTrackById(id);
        return toShowDto(track);
    }

    /**
     * Получение трека по ID (для внутренних операций)
     */
    @Transactional(readOnly = true)
    public Track getTrackById(Long id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Track not found"));
    }

    /**
     * Получение пользователя по Principal
     */
    public User getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return null;
        }
        return userRepository.findByEmail(principal.getName());
    }

    // ======================= Преобразования в DTO =======================

    private TrackPreviewDto toPreviewDto(Track track) {
        TrackPreviewDto dto = new TrackPreviewDto();
        dto.setId(track.getId());
        dto.setTitle(track.getTitle());
        dto.setArtists(track.getArtists());
        dto.setPreviewCoverId(track.getPreviewCoverId());
        dto.setArtistName(track.getUser() != null ? track.getUser().getName() : "Неизвестно");
        return dto;
    }

    private ShowTrackDto toShowDto(Track track) {
        ShowTrackDto dto = new ShowTrackDto();
        dto.setId(track.getId());
        dto.setTitle(track.getTitle());
        dto.setArtists(track.getArtists());
        dto.setGenre(track.getGenre());
        dto.setReleaseDate(track.getReleaseDate());
        dto.setLyric(track.getLyric());
        dto.setPreviewCoverId(track.getPreviewCoverId());
        dto.setArtistName(track.getUser() != null ? track.getUser().getName() : "Неизвестно");
        dto.setDateOfCreated(track.getDateOfCreated());

        if (track.getMusicFile() != null) {
            dto.setAudioFilePath(track.getMusicFile().getFilePath());
            dto.setMusicContentType(track.getMusicFile().getContentType());
        }

        // Преобразование комментариев в CommentDto
        if (track.getComments() != null && !track.getComments().isEmpty()) {
            List<CommentDto> commentDtos = track.getComments().stream()
                    .map(comment -> {
                        CommentDto commentDto = new CommentDto();
                        commentDto.setId(comment.getId());
                        commentDto.setText(comment.getText());
                        commentDto.setAuthorName(comment.getAuthor() != null ? comment.getAuthor().getName() : "Аноним");
                        commentDto.setAuthorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null);
                        return commentDto;
                    })
                    .collect(Collectors.toList());

            dto.setComments(commentDtos);
        }

        return dto;
    }

    // ======================= Остальные методы (загрузка, удаление и т.д.) =======================

    @Transactional
    public void saveTrackFromDto(AddTrackDto dto, Principal principal) throws IOException {
        User user = getUserByPrincipal(principal);
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        Track track = new Track();
        track.setTitle(dto.getTitle());
        track.setArtists(dto.getArtists());
        track.setGenre(dto.getGenre());
        track.setReleaseDate(dto.getReleaseDate());
        track.setLyric(dto.getLyric());
        track.setUser(user);

        // Сохранение аудиофайла
        if (dto.getAudio() != null && !dto.getAudio().isEmpty()) {
            MusicFile musicFile = saveAudioFile(dto.getAudio());
            track.setMusicFile(musicFile);
        }

        // Сохранение обложки (если есть)
        if (dto.getCover() != null && !dto.getCover().isEmpty()) {
            Cover cover = saveCoverFile(dto.getCover());
            track.addCoverToTrack(cover);
        }

        // Сначала сохраняем трек — это сгенерирует ID для всех связанных сущностей (Cover, MusicFile) благодаря cascade
        trackRepository.save(track);

        // Теперь устанавливаем previewCoverId, если он не был задан и есть обложки
        if (track.getPreviewCoverId() == null && !track.getCovers().isEmpty()) {
            track.setPreviewCoverId(track.getCovers().get(0).getId()); // берём первую обложку как превью
            trackRepository.save(track); // обновляем трек с новым previewCoverId
        }

        log.info("Track saved: {} by user {}. PreviewCoverId: {}",
                track.getTitle(), user.getEmail(), track.getPreviewCoverId());
    }

    private MusicFile saveAudioFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(AUDIO_DIR, fileName);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path);

        MusicFile musicFile = new MusicFile();
        musicFile.setName(file.getOriginalFilename());
        musicFile.setOriginalFileName(file.getOriginalFilename());
        musicFile.setSize(file.getSize());
        musicFile.setContentType(file.getContentType());
        musicFile.setFilePath("/files/audio/" + fileName);

        return musicFile;
    }

    private Cover saveCoverFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(COVER_DIR, fileName);
        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path);

        Cover cover = new Cover();
        cover.setName(file.getOriginalFilename());
        cover.setOriginalFileName(file.getOriginalFilename());
        cover.setSize(file.getSize());
        cover.setContentType(file.getContentType());
        cover.setFilePath("/files/covers/" + fileName);

        return cover;
    }

    @Transactional
    public void deleteTrack(Long trackId, Principal principal) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

        User currentUser = getUserByPrincipal(principal);
        if (currentUser == null) {
            throw new RuntimeException("Пользователь не авторизован");
        }

        // Проверка: админ или владелец трека
        boolean isAdmin = currentUser.getRoles().contains(Role.ROLE_ADMIN);
        boolean isOwner = track.getUser() != null && track.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("У вас нет прав на удаление этого трека");
        }

        // Очищаем избранное
        track.getFavoritedBy().forEach(user -> user.getFavorites().remove(track));

        // Удаляем трек
        trackRepository.delete(track);

        log.info("Трек удалён: id = {}, пользователь = {}", trackId, currentUser.getEmail());
    }
}