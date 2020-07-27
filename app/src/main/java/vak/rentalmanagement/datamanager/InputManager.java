package vak.rentalmanagement.datamanager;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Helps validate inputs and handle errors.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class InputManager {

    /**
     * Returns true if the input is only a number.
     * @param txt the TextInputLayout for input
     * @param error the string to display for an error.
     * @return true if the input is only numbers.
     */
    public static boolean validateNumber(TextInputLayout txt, String error) {
        String input = txt.getEditText().getText().toString().trim();
        if (!input.matches("^[0-9]*$")) {
            txt.setError(error);
            txt.setErrorEnabled(true);
            return false;
        }
        return true;
    }

    /**
     * Returns true if a number is a valid double
     * @param txt the TextInputLayout to validate
     * @param error the error to display
     * @return true if a number is a valid double
     */
    public static boolean validateDouble(TextInputLayout txt, String error) {
        try {
            Double.valueOf(txt.getEditText().getText().toString().trim());
            return true;
        } catch (Exception e) {
            txt.setError(error);
            txt.setErrorEnabled(true);
            return false;
        }
    }

    /**
     * Returns true if the TextInputLayout is not empty.
     *
     * @param txt the TextInputLayout to validate
     * @param error the error to show
     * @return true if the input is not empty.
     */
    public static boolean validateNonEmpty(TextInputLayout txt, String error) {
        String input = txt.getEditText().getText().toString().trim();
        if (input.isEmpty()) {
            txt.setError(error);
            txt.setErrorEnabled(true);
            return false;
        }
        return true;
    }
}
