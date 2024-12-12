CREATE TABLE t_task(
    id uuid primary key,
    c_details text CHECK (length(trim(c_details)) > 0 ),
    c_completed boolean not null default false
)