package Exceptions;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
public class MultipleException extends ProjectException {
    private final List<Throwable> causes;
    public MultipleException(String msg, List<Throwable> causes) {
        super(msg, causes != null && causes.size()>0 ? causes.get(0) : null);
        this.causes = causes == null ? new ArrayList<>() : new ArrayList<>(causes);
    }
    public List<Throwable> getCauses() { return Collections.unmodifiableList(causes); }
}
