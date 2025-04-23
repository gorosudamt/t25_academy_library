package jp.co.metateam.library.controller;

import java.util.List;

import org.hibernate.validator.constraints.ISBN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {
    
    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
        
        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";
    }
    

    @PostMapping("/book/add")
public String add(
    @Valid @ModelAttribute BookMstDto bookMstDto,
    BindingResult result,
    Model model
) {
    // ISBNバリデーション
    if (bookMstDto.getIsbn().trim().isEmpty()) {
        result.rejectValue("isbn", "error.value", "ISBNを入力してください");
    } else if (!bookMstDto.getIsbn().matches("[0-9]{13}")) {
        result.rejectValue("isbn", "error.value", "ISBNは13桁の半角数字で入力してください");
    }

    // 書籍名バリデーション
    if (bookMstDto.getTitle().trim().isEmpty()) {
        result.rejectValue("title", "error.value", "書籍名を入力してください");
    } else if (bookMstDto.getTitle().length() > 255) {
        result.rejectValue("title", "error.value", "書籍名は255文字以下で入力してください");
    }

    // 既存ISBNチェック（DBに登録済みか）
    List<BookMst> existingBooks = this.bookMstService.selectByIsbn(bookMstDto.getIsbn());
    if (!existingBooks.isEmpty()) {
        result.rejectValue("isbn", "error.value", "登録済みのISBNです");
    }
    
    

    // バリデーションに失敗した場合はそのままフォームへ戻る
    if (result.hasErrors()) {
        model.addAttribute("bookMstDto", bookMstDto); // 念のため
        return "book/add"; // 同じ画面を再表示
    }

    // 保存処理
    BookMst book = new BookMst();
    book.setIsbn(bookMstDto.getIsbn());
    book.setTitle(bookMstDto.getTitle());
    this.bookMstService.save(book);

    return "redirect:/book/index";
}


        
    //      catch (Exception e) {
    // //         log.error(e.getMessage());

    // //         ra.addFlashAttribute("bookMstDto", bookMstDto);
    // //         ra.addFlashAttribute("org.springframework.validation.BindingResult.bookDto", result);

    // //         return "redirect:/book/add";
    // //     }
    // // }

    
}

