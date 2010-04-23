/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import net.sourceforge.subsonic.domain.ProcessedVideo;
import static net.sourceforge.subsonic.domain.ProcessedVideo.Status;

/**
 * Provides database services for video processing.
 *
 * @author Sindre Mehus
 */
public class ProcessedVideoDao extends AbstractDao {

    private static final String COLUMNS = "id, path, source_path, log_path, quality, status, bit_rate, size";

    private final ProcessedVideoRowMapper rowMapper = new ProcessedVideoRowMapper();

    /**
     * Returns the video with the given ID.
     *
     * @param id The video ID.
     * @return The video, or {@code null} if not found.
     */
    public ProcessedVideo getProcessedVideo(int id) {
        return queryOne("select " + COLUMNS + " from processed_video where id=?", rowMapper, id);
    }

    /**
     * Returns all processed videos for the given path.
     *
     * @param sourcePath The path of the source video.
     * @return List of processed videos.
     */
    public List<ProcessedVideo> getProcessedVideos(String sourcePath) {
        return query("select " + COLUMNS + " from processed_video where source_path=?", rowMapper, sourcePath);
    }

    /**
     * Returns all processed videos with the given status.
     *
     * @param status The status.
     * @return List of processed videos.
     */
    public List<ProcessedVideo>  getProcessedVideos(Status status) {
        return query("select " + COLUMNS + " from processed_video where status=?", rowMapper, status.name());
    }

    /**
     * Creates a new processed video.
     *
     * @param video The video to create.
     */
    public void createProcessedVideo(ProcessedVideo video) {
        String sql = "insert into processed_video (" + COLUMNS + ") values (null, ?, ?, ?, ?, ?, ?, ?)";
        update(sql, video.getPath(), video.getSourcePath(), video.getLogPath(), video.getQuality(), video.getStatus().name(),
                video.getBitRate(), video.getSize());
    }

    /**
     * Updates the given processed video.
     *
     * @param video The video to update.
     */
    public void updateProcessedVideo(ProcessedVideo video) {
        String sql = "update processed_video set path=?, source_path=?, log_path=?, " +
                "quality=?, status=?, bit_rate=?, size=? where id=?";
        update(sql, video.getPath(), video.getSourcePath(), video.getLogPath(), video.getQuality(), video.getStatus().name(),
                video.getBitRate(), video.getSize(), video.getId());
    }

    /**
     * Deletes the processed video with the given ID.
     *
     * @param id The video ID.
     */
    public void deleteProcessedVideo(int id) {
        String sql = "delete from processed_video where id=?";
        update(sql, id);
    }

    private static class ProcessedVideoRowMapper implements ParameterizedRowMapper<ProcessedVideo> {
        public ProcessedVideo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ProcessedVideo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                    Status.valueOf(rs.getString(6)), rs.getInt(7), rs.getLong(8));
        }
    }
}