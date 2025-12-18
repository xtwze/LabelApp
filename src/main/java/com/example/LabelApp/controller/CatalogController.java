package com.example.LabelApp.controller;

import com.example.LabelApp.dto.AddTrackDto;
import com.example.LabelApp.dto.ShowTrackDto;
import com.example.LabelApp.dto.TrackPreviewDto;
import com.example.LabelApp.models.Track;
import com.example.LabelApp.models.User;
import com.example.LabelApp.services.CommentService;
import com.example.LabelApp.services.TrackService;
import com.example.LabelApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final TrackService trackService;
    private final UserService userService;
    private final CommentService commentService;

    /**
     * Главная страница — каталог треков
     */
    @GetMapping("/")
    public String showTracks(
            @RequestParam(name = "title", required = false) String title,
            Principal principal,
            Model model) {

        List<TrackPreviewDto> previews = trackService.listTrackPreviews(title);
        model.addAttribute("tracks", previews);
        model.addAttribute("user", trackService.getUserByPrincipal(principal));
        return "catalog";
    }

    /**
     * Страница детальной информации о треке
     */
    @GetMapping("/track/{id}")
    public String trackInfo(@PathVariable Long id, Model model, Principal principal) {
        ShowTrackDto trackDto = trackService.getShowTrackDtoById(id);
        model.addAttribute("track", trackDto);

        User currentUser = trackService.getUserByPrincipal(principal);
        model.addAttribute("user", currentUser);

        // Является ли текущий пользователь владельцем трека
        boolean isOwner = currentUser != null
                && trackDto.getArtistName() != null
                && trackDto.getArtistName().equals(currentUser.getName());
        model.addAttribute("isOwner", isOwner);

        // Проверяем, в избранном ли трек у текущего пользователя
        Track track = trackService.getTrackById(id);
        boolean isFavorite = currentUser != null && currentUser.getFavorites().contains(track);
        model.addAttribute("isFavorite", isFavorite);

        return "track-info";
    }

    /**
     * Форма добавления трека
     */
    @GetMapping("/track/add")
    @PreAuthorize("isAuthenticated()")
    public String showAddForm(Model model) {
        model.addAttribute("track", new AddTrackDto());
        return "track-add";
    }

    /**
     * Сохранение нового трека
     */
    @PostMapping("/track/create")
    @PreAuthorize("isAuthenticated()")
    public String createTrack(
            @ModelAttribute("track") AddTrackDto dto,
            Principal principal
    ) throws IOException {

        trackService.saveTrackFromDto(dto, principal);
        return "redirect:/";
    }

    /**
     * Удаление трека (только админ или владелец)
     */
    @PostMapping("/track/delete/{id}")
    @PreAuthorize("isAuthenticated()") // только авторизованный
    public String deleteTrack(@PathVariable Long id, Principal principal) {
        trackService.deleteTrack(id, principal); // передаём principal для проверки
        return "redirect:/";
    }

    /**
     * Добавление/удаление из избранного
     */
    @PostMapping("/track/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    public String toggleFavorite(@PathVariable Long id, Principal principal) {
        User user = trackService.getUserByPrincipal(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Track track = trackService.getTrackById(id);

        if (user.getFavorites().contains(track)) {
            userService.removeFromFavorites(user, track);
        } else {
            userService.addToFavorites(user, track);
        }

        return "redirect:/track/" + id;
    }

    /**
     * Добавление комментария
     */
    @PostMapping("/track/{id}/comment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(
            @PathVariable Long id,
            @RequestParam("text") String text,
            Principal principal) {

        User user = trackService.getUserByPrincipal(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Track track = trackService.getTrackById(id);
        commentService.createComment(text, user, track);

        return "redirect:/track/" + id;
    }

    /**
     * Страница избранных треков пользователя
     */
    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public String favorites(Principal principal, Model model) {
        User currentUser = trackService.getUserByPrincipal(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Получаем все избранные треки и преобразуем в TrackPreviewDto
        List<TrackPreviewDto> favoritePreviews = currentUser.getFavorites().stream()
                .map(this::convertTrackToPreviewDto)
                .sorted((a, b) -> b.getId().compareTo(a.getId())) // опционально: новые сверху
                .collect(Collectors.toList());

        model.addAttribute("tracks", favoritePreviews);
        model.addAttribute("user", currentUser);

        return "favorites";
    }

    /**
     * Вспомогательный метод для преобразования Track в TrackPreviewDto
     * (дублирует логику из TrackService, чтобы не тянуть весь сервис)
     */
    private TrackPreviewDto convertTrackToPreviewDto(Track track) {
        TrackPreviewDto dto = new TrackPreviewDto();
        dto.setId(track.getId());
        dto.setTitle(track.getTitle());
        dto.setArtists(track.getArtists());
        dto.setPreviewCoverId(track.getPreviewCoverId());
        dto.setArtistName(track.getUser() != null ? track.getUser().getName() : "Неизвестно");
        return dto;
    }
}