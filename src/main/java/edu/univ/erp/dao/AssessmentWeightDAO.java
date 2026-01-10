package edu.univ.erp.dao;

import edu.univ.erp.domain.AssessmentWeights;
import edu.univ.erp.exception.DataAccessException;
import edu.univ.erp.util.DbConnection;
import edu.univ.erp.util.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class AssessmentWeightDAO {

    private final DataSource dataSource;
    private static final Logger logger = Logger.getLogger(AssessmentWeightDAO.class);

    public AssessmentWeightDAO() {
        this.dataSource = DbConnection.getDataSource();
    }

    public AssessmentWeights findBySectionId(int sectionId) {
        final String sql = "SELECT section_id, quiz_weight, midterm_weight, endsem_weight, updated_at FROM section_assessment_weights WHERE section_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error loading assessment weights for section {}", sectionId, e);
            throw new DataAccessException("Unable to load assessment weights", e);
        }
        return null;
    }

    public boolean upsert(AssessmentWeights weights) {
        final String sql = """
                INSERT INTO section_assessment_weights (section_id, quiz_weight, midterm_weight, endsem_weight)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    quiz_weight = VALUES(quiz_weight),
                    midterm_weight = VALUES(midterm_weight),
                    endsem_weight = VALUES(endsem_weight)
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, weights.getSectionId());
            ps.setDouble(2, weights.getQuizWeight());
            ps.setDouble(3, weights.getMidtermWeight());
            ps.setDouble(4, weights.getEndSemWeight());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error saving assessment weights for section {}",
                    weights != null ? weights.getSectionId() : "null", e);
            throw new DataAccessException("Unable to save assessment weights", e);
        }
    }

    private AssessmentWeights map(ResultSet rs) throws SQLException {
        AssessmentWeights weights = new AssessmentWeights();
        weights.setSectionId(rs.getInt("section_id"));
        weights.setQuizWeight(rs.getDouble("quiz_weight"));
        weights.setMidtermWeight(rs.getDouble("midterm_weight"));
        weights.setEndSemWeight(rs.getDouble("endsem_weight"));
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            weights.setUpdatedAt(ts.toLocalDateTime());
        }
        return weights;
    }
}
