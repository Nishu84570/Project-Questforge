
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import questforge.model.Option;

import java.util.List;

@Repository
public interface OptionRepository
        extends JpaRepository<Option, Long> {

    List<Option> findByQuestionId(Long questionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Option o WHERE o.questionId = :questionId")
    int deleteByQuestionId(@Param("questionId") Long questionId);
}
