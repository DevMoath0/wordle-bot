# Wordle Telegram Bot 🎮🤖

Welcome to the **Wordle Telegram Bot** repository! This bot allows you and your friends to track your daily Wordle scores and compete in a leaderboard directly through Telegram. The bot tracks results, calculates scores, and handles missed entries, all while providing a fun and interactive experience.

---

## Features 🚀

- **Result Submission**: Players can send their daily Wordle results, and the bot records them.
- **Leaderboard**: At any time, users can request the current leaderboard with the `/leaderboard` command.
- **Missed Results**: If a player fails to submit their result on time, they get a penalty.
- **Daily Processing**: The bot automatically processes the results at the end of each day, updating the leaderboard and assigning points.
- **Flexible Score Calculation**: The player with the highest tries gets +1 point, and those who miss a submission get +2 points.
- **Interactive Commands**: Players can use `/help` to see available commands like `/leaderboard`.

---

## Technologies Used 🛠️

- **Java 17**
- **Spring Boot** - Backend framework
- **JPA & Hibernate** - ORM for database interactions
- **Telegram Bot API** - Telegram Bot integration
- **MySQL** - Database to store players' results and scores
- **Lombok** - For cleaner code with less boilerplate
- **Scheduled Tasks** - For automatic daily processing at 11:59 PM
