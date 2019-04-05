package de.tutorial.model;

public class ErrorResponse {
    private final String reason;
    private final String hint;

    public ErrorResponse(final String reason, final String hint) {
        this.reason = reason;
        this.hint = hint;
    }

    public String getReason() {
        return reason;
    }

    public String getHint() {
        return hint;
    }
}
