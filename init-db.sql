-- SMS Database Initialization Script
-- Creates all tables based on the class diagram

-- Administrator Table
CREATE TABLE IF NOT EXISTS administrators (
    admin_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Degree Program Table
CREATE TABLE IF NOT EXISTS degree_programs (
    degree_program_id BIGSERIAL PRIMARY KEY,
    degree_name VARCHAR(150) NOT NULL,
    department_name VARCHAR(150) NOT NULL,
    credit_value INT NOT NULL,
    duration_years INT NOT NULL
);

-- Student Table
CREATE TABLE IF NOT EXISTS students (
    student_id BIGSERIAL PRIMARY KEY,
    student_number VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    date_of_birth DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    degree_program_id BIGINT REFERENCES degree_programs(degree_program_id)
);

-- Course Table
CREATE TABLE IF NOT EXISTS courses (
    course_id BIGSERIAL PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(150) NOT NULL,
    credit_value INT NOT NULL,
    semester INT NOT NULL
);

-- Enrollment Table
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id BIGSERIAL PRIMARY KEY,
    academic_year VARCHAR(20) NOT NULL,
    semester INT NOT NULL,
    enrollment_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    student_id BIGINT NOT NULL REFERENCES students(student_id),
    course_id BIGINT NOT NULL REFERENCES courses(course_id),
    CONSTRAINT unique_enrollment UNIQUE (student_id, course_id, academic_year, semester)
);

-- Audit Log Table
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id BIGSERIAL PRIMARY KEY,
    action_type VARCHAR(100) NOT NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    admin_id BIGINT REFERENCES administrators(admin_id)
);

-- Default Admin User (legacy bcrypt hash for "password")
INSERT INTO administrators (username, email, password_hash, role)
VALUES ('admin', 'admin@sms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Sample Degree Programs
INSERT INTO degree_programs (degree_name, department_name, credit_value, duration_years)
VALUES
  ('Bachelor of Computer Science', 'Computer Science', 120, 3),
  ('Bachelor of Information Technology', 'Information Technology', 120, 3),
  ('Bachelor of Software Engineering', 'Software Engineering', 130, 4)
ON CONFLICT DO NOTHING;
