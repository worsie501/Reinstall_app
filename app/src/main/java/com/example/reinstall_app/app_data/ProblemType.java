package com.example.reinstall_app.app_data;

public class ProblemType {

    String problemName;

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    @Override
    public String toString()
    {
        return problemName;
    }
}

