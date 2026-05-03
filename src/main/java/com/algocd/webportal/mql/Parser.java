package com.algocd.webportal.mql;

import com.algocd.webportal.mql.tree.Statement;

public interface Parser {

    Statement[] parse();
}
