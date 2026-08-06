package com.matusalenalves.library.config;

import com.matusalenalves.library.entities.Author;
import com.matusalenalves.library.entities.Book;
import com.matusalenalves.library.entities.Category;
import com.matusalenalves.library.entities.Loan;
import com.matusalenalves.library.entities.User;
import com.matusalenalves.library.entities.enums.Role;
import com.matusalenalves.library.repositories.AuthorRepository;
import com.matusalenalves.library.repositories.BookRepository;
import com.matusalenalves.library.repositories.CategoryRepository;
import com.matusalenalves.library.repositories.LoanRepository;
import com.matusalenalves.library.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

/**
 * Popula o banco de dados com dados de exemplo ao iniciar a aplicação.
 * <p>
 * Útil apenas em ambiente de desenvolvimento local, para testar as camadas
 * de repository/service/controller sem precisar cadastrar dados manualmente
 * a cada reinício. Não deve rodar em produção.
 */
@Configuration
public class TestConfig implements CommandLineRunner {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Override
    public void run(String... args) throws Exception {

        Author machado = new Author(null, "Machado de Assis");
        Author orwell = new Author(null, "George Orwell");
        Author tolkien = new Author(null, "J.R.R. Tolkien");
        authorRepository.saveAll(List.of(machado, orwell, tolkien));

        Category fiction = new Category(null, "Fiction");
        Category classics = new Category(null, "Classics");
        Category fantasy = new Category(null, "Fantasy");
        Category dystopia = new Category(null, "Dystopia");
        categoryRepository.saveAll(List.of(fiction, classics, fantasy, dystopia));

        Book domCasmurro = new Book(null, "Dom Casmurro", "9788535910663", 1899, 3, machado);
        domCasmurro.setCategories(Set.of(fiction, classics));

        Book nineteenEightyFour = new Book(null, "1984", "9780451524935", 1949, 5, orwell);
        nineteenEightyFour.setCategories(Set.of(fiction, dystopia));

        Book theHobbit = new Book(null, "The Hobbit", "9780547928227", 1937, 4, tolkien);
        theHobbit.setCategories(Set.of(fiction, fantasy));

        bookRepository.saveAll(List.of(domCasmurro, nineteenEightyFour, theHobbit));

        // TODO: password em texto plano por enquanto — trocar para hash BCrypt
        // (RNF05) assim que a configuração do Spring Security/JWT for feita.
        User admin = new User(null, "Matusalen Alves", "admin@library.com", "admin123", Role.ADMIN);
        User client = new User(null, "Maria Silva", "maria@library.com", "client123", Role.CLIENT);
        userRepository.saveAll(List.of(admin, client));

        Loan loan1 = new Loan(null, nineteenEightyFour, client);
        nineteenEightyFour.decreaseAvailableCopies();

        Loan loan2 = new Loan(null, theHobbit, client);
        theHobbit.decreaseAvailableCopies();

        loanRepository.saveAll(List.of(loan1, loan2));
        bookRepository.saveAll(List.of(nineteenEightyFour, theHobbit));
    }
}