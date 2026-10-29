package lv.macibas.library.service;

import lv.macibas.library.domain.Author;
import lv.macibas.library.dto.AuthorRequest;
import lv.macibas.library.dto.AuthorResponse;
import lv.macibas.library.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorResponse> findAll() {
        return authorRepository.findAllByOrderByNameAsc().stream()
                .map(AuthorResponse::from)
                .toList();
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        Author saved = authorRepository.save(new Author(request.name().trim()));
        return AuthorResponse.from(saved);
    }
}
