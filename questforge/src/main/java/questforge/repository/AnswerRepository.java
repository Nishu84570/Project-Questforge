package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import questforge.model.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    List<Answer> findByAttemptId(Integer attemptId);

    boolean existsByAttemptIdAndQuestionId(
            Integer attemptId,
            Integer questionId
    );
}