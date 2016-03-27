# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus

## ITB Map Navigation

# Deskripsi Aplikasi
Aplikasi ini adalah aplikasi yang menampilkan peta untuk menemukan beberapa tempat di lingkungan ITB. Aplikasi ini dilengkapi dengan fitur kamera dan fitur tebak-tebakan lokasi. Adapun jawaban lokasi yang mungkin adalah :
* gku_barat
* gku_timur
* intel
* cc_barat
* cc_timur
* dpr
* oktagon
* perpustakaan
* pau
* kubus

## Spesifikasi Tampilan Aplikasi
# Tampilan Awal

**Potrait**
![alt text](http://gitlab.informatika.org/13513031/Tubes1-Android/raw/20478f4d186336f1b551a53b4741e81b743a2939/res/potrait.png)

**Landscape**
![alt text](http://gitlab.informatika.org/13513031/Tubes1-Android/raw/5fc7f1513a76f6c4bc4e0ac0d64a0513835b5bf2/res/landscape.png)

**Camera**
![alt text](http://gitlab.informatika.org/13513031/Tubes1-Android/raw/20478f4d186336f1b551a53b4741e81b743a2939/res/camera.png)

**Log Page**
![alt text](http://gitlab.informatika.org/13513031/Tubes1-Android/raw/20478f4d186336f1b551a53b4741e81b743a2939/res/log.png)

**Submit Answer**
![alt text](http://gitlab.informatika.org/13513031/Tubes1-Android/raw/20478f4d186336f1b551a53b4741e81b743a2939/res/submit_ans.png)

**Correct Answer**
![alt text](http://gitlab.informatika.org/13513031/Tubes1-Android/raw/20478f4d186336f1b551a53b4741e81b743a2939/res/answer_correct.png)

## Asumsi
1. Tidak mungkin nim dan com command hilang di tengah-tengah. Inisialisasi yang salah pada MapActivity otomatis mengakibatkan aplikasi keluar jadi tidak mungkin masuk ke SubmitAnswerActivity.
2. Longitude dan Latitude yang dikirimkan client adalah posisi client saat ini dengan marker berwarna hijau.
3. Log request dan response ditampilkan pada halaman log (bukan di komputer).
4. Semua response ditampilkan juga di Toast.

## Lokasi File
# bin
\app\build\outputs\apk

# src
\app\src\main\java\com\candy\myapplication

# res
\app\src\main\res