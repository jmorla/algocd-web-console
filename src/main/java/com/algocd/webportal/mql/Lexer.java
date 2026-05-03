package com.algocd.webportal.mql;

/**
 * The Lexical Analizer interface designed to map an input stream of characters
 */
public interface Lexer {

    /**
     * Retrieves the next token from the input source.
     *
     *  @return The next token from the input source.
     */
    Result<Token, SyntaxError> getToken();


    /**
     * Retrieves a token from the input source with a specified lookahead.
     *
     * @param lookahead The number of tokens to lookahead
     * @return The token at the current position with the specified lookahead.
     */
    Result<Token, SyntaxError> getToken(int lookahead);


    /**
     * Consume the next token.
     */
    void nextToken();

}