@echo off

REM Parameters:
REM ----------
REM 1: Source video file.
REM 2: Target video file.
REM 3: Log file.
REM 4: Transcoding directory

%4/ffmpeg -i %1 -y -vcodec libx264 %2