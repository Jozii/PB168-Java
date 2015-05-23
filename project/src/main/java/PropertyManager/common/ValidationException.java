package PropertyManager.common;

/**
 * Created by Jozef Živčic on 30. 3. 2015.
 */
public class ValidationException extends RuntimeException {

        public ValidationException() {
        }

        public ValidationException(String msg) {
            super(msg);
        }

}
