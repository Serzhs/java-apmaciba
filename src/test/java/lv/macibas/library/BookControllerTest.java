package lv.macibas.library;

import lv.macibas.library.dto.BookResponse;
import lv.macibas.library.exception.NotFoundException;
import lv.macibas.library.service.BookService;
import lv.macibas.library.service.LoanService;
import lv.macibas.library.web.BookController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WEB SLĀŅA TESTS (@WebMvcTest) — palaiž TIKAI web daļu (kontrolieri, JSON, validāciju, kļūdu handleri).
 * Serviss ir mock. Nav DB. Pārbauda: URL, statusa kodus, JSON formu.
 *
 * MockMvc = "viltus pārlūks", kas sūta HTTP pieprasījumus bez īsta servera.
 */
@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean BookService bookService;
    @MockitoBean LoanService loanService;

    @Test
    void getBook_returns200AndJson() throws Exception {
        when(bookService.findById(1L))
                .thenReturn(new BookResponse(1L, "Dune", "9780441013593", 1965, true, 7L, "Frank Herbert"));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune"))
                .andExpect(jsonPath("$.authorName").value("Frank Herbert"));
    }

    @Test
    void getBook_notFound_returns404ProblemDetail() throws Exception {
        when(bookService.findById(99L)).thenThrow(new NotFoundException("Grāmata", 99L));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Grāmata ar id=99 nav atrasts"));
    }

    @Test
    void createBook_invalidBody_returns400WithFieldErrors() throws Exception {
        String invalidJson = """
                { "title": "", "isbn": "abc", "authorId": null }
                """;

        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.isbn").value("ISBN jābūt 10 vai 13 cipariem"))
                .andExpect(jsonPath("$.errors.authorId").exists());
    }

    @Test
    void malformedJson_returns400_not500() throws Exception {
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content("{ šis nav json"))
                .andExpect(status().isBadRequest());
    }
}
