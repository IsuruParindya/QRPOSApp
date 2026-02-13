# QR POS App

A desktop **Java Swing POS application** that uses **QR codes** to identify products, fetch details from a **SQLite database**, generate **QR and label previews**, and print product labels.

This project is designed to simulate a **real-world retail / POS workflow** using a QR scanner (keyboard-emulated), database-backed products, and printable labels.

---

## ✨ Features

- 📷 **QR Code Scanning**
  - Works with USB / Bluetooth QR scanners (scanner acts as a keyboard)
  - Manual entry supported for testing

- 🗄 **SQLite Database Integration**
  - Stores products using QR code as the primary key
  - Supports lookup, insert, and update

- 🖼 **Live Previews**
  - QR Preview (generated QR image)
  - Label Preview (QR + product name + price)

- 🖨 **Label Printing**
  - Print-ready label using Java `PrinterJob`
  - Scales to fit printer page

- 🧹 **POS-Friendly UI**
  - Status bar feedback (no popup spam)
  - One-click clear/reset
  - Keyboard-driven workflow

---

## 🖥 Application UI Overview

### Left Panel (Operator Side)
- **Scan QR** – receives QR input from scanner or keyboard
- **Product** – product name (auto-filled or manual)
- **Price** – product price (auto-filled or manual)
- **Actions**
  - Lookup (DB)
  - Generate Preview
  - Print Label
  - Save / Update (DB)
  - Clear
- **Status Bar** – shows system messages and errors

### Right Panel (Preview Side)
- **QR Preview** – generated QR image
- **Label Preview** – final printable label

<img width="1076" height="642" alt="Screenshot (338)" src="https://github.com/user-attachments/assets/04624312-a697-43fb-aecb-62247a25cbd1" />

---

## 🛠 Technologies Used

- **Java 17+**
- **Java Swing** (Desktop UI)
- **SQLite** (Embedded database)
- **ZXing** (QR code generation)
- **Maven** (Dependency management)

---

## 🚀 How to Run

### Prerequisites
- Java JDK installed
- Maven installed
- Git (optional, for cloning)

## Bug Fix (Done)

### Run with Maven
```bash
mvn clean compile
mvn exec:java
