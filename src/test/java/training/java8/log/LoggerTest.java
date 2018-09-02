package training.java8.log;

import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static training.java8.log.Logger.ERROR;

public class LoggerTest {

    public static final int ONE = 1;
    private MessageFormatter messageFormatter;
    private PrintStream printer;
    private Logger logger;

    @Before
    public void setUp() throws Exception {
        logger = new Logger(ERROR, printer);
        printer = mock(PrintStream.class);
        messageFormatter = mock(MessageFormatter.class);
        given(messageFormatter.format(anyString())).willReturn("String");
    }

    @Test
    public void not_lazy_logger_error_when_log_debug() throws Exception {
        logger.debug(messageFormatter.format("Non important message."));

        verify(messageFormatter, times(ONE)).format(anyString());
        verify(printer, never()).println(anyString());
    }

    @Test
    public void not_lazy_logger_error_when_log_error() throws Exception {
        logger.error(messageFormatter.format("Very important message."));

        verify(messageFormatter, times(ONE)).format(anyString());
        verify(printer, times(ONE)).println(anyString());
    }

    /*
    *   Try to avoid call to MessageFormatter.format
    *   Tip: use Supplier functions.
    */

    @Test
    public void lazy_logger_error_when_log_debug() throws Exception {
        logger.debug(messageFormatter.format("Non important message."));

        verify(messageFormatter, never()).format(anyString());
        verify(printer, never()).println(anyString());
    }

    @Test
    public void lazy_logger_error_when_log_error() throws Exception {
        logger.error(messageFormatter.format("Very important message."));

        verify(messageFormatter, times(ONE)).format(anyString());
        verify(printer, times(ONE)).println(anyString());
    }

}