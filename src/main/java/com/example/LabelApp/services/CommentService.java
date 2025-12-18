package com.example.LabelApp.services;

import com.example.LabelApp.models.Comment;
import com.example.LabelApp.models.Track;
import com.example.LabelApp.models.User;
import com.example.LabelApp.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * Создаёт и сохраняет новый комментарий под треком
     *
     * @param text   текст комментария
     * @param author пользователь, который пишет комментарий
     * @param track  трек, к которому относится комментарий
     */
    @Transactional
    public void createComment(String text, User author, Track track) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }

        Comment comment = new Comment();
        comment.setText(text.trim());
        comment.setAuthor(author);
        comment.setTrack(track);

        commentRepository.save(comment);

        log.info("Новый комментарий добавлен: userId={}, trackId={}, commentId={}",
                author.getId(), track.getId(), comment.getId());
    }

    /**
     * Возвращает список всех комментариев к конкретному треку
     * (используется при формировании ShowTrackDto в TrackService)
     *
     * @param trackId ID трека
     * @return список комментариев (без сортировки — сортировка делается в TrackService)
     */
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByTrackId(Long trackId) {
        return commentRepository.findByTrackId(trackId);
    }
}