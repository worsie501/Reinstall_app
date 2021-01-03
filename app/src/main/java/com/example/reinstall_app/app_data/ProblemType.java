package com.example.reinstall_app.app_data;

public class ProblemType {

    String problemName;
    int totalProblems;


    public int getTotalProblems() {
        return totalProblems;
    }

    public void setTotalProblems(int totalProblems) {
        this.totalProblems = totalProblems;
    }

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

