package jp.co.metateam.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;

import java.util.List;
import java.util.Optional;

public interface BookMstRepository extends JpaRepository<BookMst, Long> {

	@Query(value = "SELECT * FROM book_mst LIMIT 1000", nativeQuery = true)
	List<BookMst> findLimitedBook();

	@Query(value = "SELECT * FROM book_mst WHERE id = ?1", nativeQuery = true)
	Optional<BookMst> selectById(Long id);

	@Query("SELECT b FROM BookMst b WHERE b.isbn = :isbn")
	List<BookMst> findByIsbn(@Param("isbn") String isbn);

	@Query(value = "SELECT * FROM book_mst ", nativeQuery = true)
	Optional<BookMst> selectByTitle(String title);

}
