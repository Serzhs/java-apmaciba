package lv.macibas.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * INTEGRĀCIJAS TESTS (@SpringBootTest) — palaiž VISU aplikāciju: kontrolieri + servisi + īsta (H2) DB.
 * Lēnāks, bet pārbauda, ka viss strādā KOPĀ. Kā Cypress/Playwright tests, tikai bez UI.
 *
 * Testu piramīda: daudz unit testu (ātri) -> mazāk slāņa testu -> nedaudz integrācijas/E2E (lēni).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // izslēdz DataInitializer -> tukša DB
class LibraryIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void fullFlow_createAuthorAndBook_borrow_cannotBorrowTwice_return() throws Exception {
        // 1. Izveido autoru
        long authorId = idFrom(mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Frank Herbert\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        // 2. Izveido grāmatu
        long bookId = idFrom(mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Dune","isbn":"9780441013593","publishedYear":1965,"authorId":%d}
                                """.formatted(authorId)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().exists("X-Request-Id")) // mūsu filtrs nostrādāja
                .andReturn().getResponse().getContentAsString());

        // 3. Tas pats ISBN otrreiz -> 409
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Dune 2","isbn":"9780441013593","publishedYear":1969,"authorId":%d}
                                """.formatted(authorId)))
                .andExpect(status().isConflict());

        // 4. Aizņemas
        long loanId = idFrom(mockMvc.perform(post("/api/books/{id}/loans", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"borrowerName\":\"Jānis\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookTitle").value("Dune"))
                .andReturn().getResponse().getContentAsString());

        // 5. Otrs mēģinājums -> 409, jo jau izsniegta
        mockMvc.perform(post("/api/books/{id}/loans", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"borrowerName\":\"Anna\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(jsonPath("$.available").value(false));

        // 6. Atdod -> atkal pieejama
        mockMvc.perform(post("/api/loans/{id}/return", loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnedAt").exists());

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void unknownBook_returns404() throws Exception {
        mockMvc.perform(get("/api/books/12345"))
                .andExpect(status().isNotFound());
    }

    private long idFrom(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        return node.get("id").asLong();
    }
}
