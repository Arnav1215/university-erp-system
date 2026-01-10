package edu.univ.erp.domain;

import java.time.LocalDateTime;

public class AssessmentWeights {
    private int sectionId;
    private double quizWeight;
    private double midtermWeight;
    private double endSemWeight;
    private LocalDateTime updatedAt;

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public double getQuizWeight() {
        return quizWeight;
    }

    public void setQuizWeight(double quizWeight) {
        this.quizWeight = quizWeight;
    }

    public double getMidtermWeight() {
        return midtermWeight;
    }

    public void setMidtermWeight(double midtermWeight) {
        this.midtermWeight = midtermWeight;
    }

    public double getEndSemWeight() {
        return endSemWeight;
    }

    public void setEndSemWeight(double endSemWeight) {
        this.endSemWeight = endSemWeight;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getTotalWeight() {
        return quizWeight + midtermWeight + endSemWeight;
    }
}
