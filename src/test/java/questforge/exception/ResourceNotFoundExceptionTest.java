
package questforge.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_ShouldStoreExceptionMessage() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException("User not found");

        assertEquals("User not found", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }
}
