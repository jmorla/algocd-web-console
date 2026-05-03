package com.algocd.webportal.mql;

import java.util.ArrayList;
import java.util.List;

public class MqlScanner implements Lexer {
    private final MqlTokenizer tokenizer;
    private final List<Result<Token, SyntaxError>> buffer = new ArrayList<>();
    private int position = 0;
    private boolean eofReached = false;

    public MqlScanner(MqlTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public Result<Token, SyntaxError> getToken() {
        return getToken(0);
    }

    @Override
    public Result<Token, SyntaxError> getToken(int lookahead) {
        int targetIndex = position + lookahead;
        while (buffer.size() <= targetIndex && !eofReached) {
            Result<Token, SyntaxError> result = tokenizer.readToken();
            buffer.add(result);
            if (!result.isSuccess() || result.getValue().kind == Token.TokenKind.EOF) {
                eofReached = true;
            }
        }
        
        if (targetIndex >= buffer.size()) {
            // Return the last buffered result (EOF or Error) if we try to peek past it
            return buffer.getLast();
        }
        
        return buffer.get(targetIndex);
    }

    @Override
    public void nextToken() {
        // Ensure current token is loaded
        getToken(0);
        
        Result<Token, SyntaxError> current = buffer.get(position);
        if (current.isSuccess() && current.getValue().kind != Token.TokenKind.EOF) {
            position++;
        }
    }
}
