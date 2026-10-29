package lv.macibas.library.repository;

import lv.macibas.library.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAllByOrderByNameAsc();
}
