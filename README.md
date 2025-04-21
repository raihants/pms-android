Siap, berikut versi `README.md` **tanpa format markdown khusus seperti bash block, dll**, jadi tampilannya bersih dan mudah dibaca langsung di GitHub atau teks editor:

---

## 🚦 Aturan Git (Commit, Push, Pull)

Dokumen ini berisi aturan kerja Git untuk seluruh anggota tim agar kerja tetap rapi, terstruktur, dan tidak saling menimpa.

---

### 1. Struktur Kerja

- Setiap anggota **harus kerja di branch masing-masing**, sesuai fitur.
- Branch utama adalah: `main`
- Dilarang langsung push ke `main` tanpa review (gunakan Pull Request).
- Backend dan Android dipisah di folder:  
  - `/pms-android/` → Android Studio  
  - `/pms-backend/` → PHP API

---

### 2. Nama Branch

Gunakan format:

`feature-nama-fitur`  
`fix-nama-bug`

Contoh:
- `feature-login`
- `feature-api-tugas`
- `fix-status-tugas-error`

Cara buat branch baru:

`git checkout -b feature-nama-fitur`

---

### 3. Format Commit

Gunakan format berikut:

`[fitur/fix] deskripsi singkat`

Contoh:
- `[fitur] tampilkan daftar proyek`
- `[fix] perbaiki crash saat klik tombol simpan`

---

### 4. Cara Push

Langkah:
- Tambahkan semua perubahan: `git add .`
- Buat commit: `git commit -m "[fitur] buat halaman tugas"`
- Push ke branch: `git push origin nama-branch`

Contoh:

`git push origin feature-tugas`

---

### 5. Cara Pull

Selalu lakukan **pull sebelum mulai kerja**:

`git pull origin main`

Jika branch kamu belum update dengan `main`, lakukan merge:

- `git checkout nama-branch`
- `git merge main`

---

### 6. Pull Request (PR)

- Setelah selesai fitur, buat **Pull Request ke branch `main`**
- PR akan direview dulu sebelum merge
- Tambahkan deskripsi fitur yang dibuat dalam PR

---

### 7. Jangan Upload File Ini

Pastikan `.gitignore` memblokir file berikut:

**Android:**
- `.gradle/`
- `/build/`
- `.idea/`
- `*.apk`
- `local.properties`

**PHP:**
- `config/db.php`
- `.env`
- `/vendor/`
- `*.log`

---

### 8. Komunikasi Tim

- Lapor ke grup jika:
  - Sudah push ke GitHub
  - Ada bug/error
  - Selesai fitur

- Setiap hari kerja, **pull dulu**, baru coding!

---

**Jaga repo tetap bersih, kerja tim lebih lancar! 🚀**

---

Kalau kamu mau, aku bisa buatin file `.md`-nya langsung buat diunduh atau ditaruh ke repositorimu. Mau lanjut ke `.gitignore` juga?
