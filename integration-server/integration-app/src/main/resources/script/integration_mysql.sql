CREATE TABLE IF NOT EXISTS t_st_job_schedule (
    id BIGINT NOT NULL PRIMARY KEY,
    job_define_id BIGINT NOT NULL,
    schedule_name VARCHAR(255) NOT NULL,
    crontab VARCHAR(128) NOT NULL,
    schedule_type VARCHAR(32) NOT NULL DEFAULT 'INTERNAL',
    enabled TINYINT NOT NULL DEFAULT 1,
    ds_project_code VARCHAR(64) DEFAULT NULL,
    ds_process_definition_code BIGINT DEFAULT NULL,
    ds_schedule_id INT DEFAULT NULL,
    last_trigger_time DATETIME DEFAULT NULL,
    create_user_id INT DEFAULT NULL,
    update_user_id INT DEFAULT NULL,
    create_time DATETIME DEFAULT NULL,
    update_time DATETIME DEFAULT NULL,
    workspace_id BIGINT DEFAULT NULL,
    UNIQUE KEY uk_job_define_workspace (job_define_id, workspace_id)
);
