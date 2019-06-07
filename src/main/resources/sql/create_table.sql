CREATE TABLE IF NOT EXISTS notes
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    date     Date,
    time     Time,
    location varchar,
    title    varchar,
    details  varchar,
    color    varchar
)