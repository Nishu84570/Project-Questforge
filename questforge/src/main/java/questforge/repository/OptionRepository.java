package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import questforge.model.Option;

import java.util.List;

public interface OptionRepository
        extends JpaRepository<Option, Integer> {

    List<Option> findByQuestionId(Integer questionId);

    @Modifying
    @Transactional
    void deleteByQuestionId(Integer questionId);
}