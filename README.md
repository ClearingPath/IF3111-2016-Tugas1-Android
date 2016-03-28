# ITB Locator

![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/ITBLocator-01.png)

ITB Locator adalah sebuah aplikasi android sederhana yanng menampilkan marker pada peta ITB.
Tugas pengguna adalah untuk menyelesaikan tebakan tempat marker berada di ITB.

Daftar kemungkinan jawaban lokasi adalah:
- GKU Barat
- GKU Timur
- Intel
- CC Barat
- CC Timur
- DPR
- Sunken
- Perpustakaan
- PAU
- Kubus

Adapun pilihan spesifikasi yang ada pada aplikasi adalah sebagai berikut:
* Aplikasi mampu menerima pesan dari server dengan format JSON berisi lokasi dan token.
* Aplikasi mampu mengolah data berupa location point (longitude, latitude) dan menampilkan indicator pada peta lokasi yang dimaksud. Peta lokasi menggunakan Google Map API. (Tampilan silahkan lihat Spesifikasi Tampilan).
* Terdapat sebuah panah navigasi yang berada diatas peta (letak bebas), yang menunjukan arah utara. Anda diminta menggunakan sensor yang ada pada android API (Tampilan silahkan lihat Spesifikasi Tampilan).
* Aplikasi mampu mengirim intent kamera.
* Aplikasi mampu mengambil gambar melalui kamera. Gambar tidak perlu diunggah ke server (silakan lihat spesifikasi tanya jawab asisten)
* Aplikasi mampu mengirimkan pesan ke sever dengan format JSON berisi lokasi (longitude, latitude), nim serta token.
* Pastikan SDK anda mendukung pengerjaan tugas ini.
* Perhatikan tata letak tombol. Ketika orientasi layar portrait, tombol berada pada bawah layar. Ketika landscape, tombol berada pada samping kanan layar (lihat contoh tampilan seperti pada mock-up spesifikasi tampilan). Anda dapat menggunakan fragment untuk masalah ini.
* Tampilan warna, font, style tidak dinilai. Namun tata letak tombol akan dinilai.
* Hasil reply dari server harus ditampilkan dalam bentuk *toast* atau *alert dialog* (pilih satu).
* Ketika anda menggunakan activity yang memanggil sensor, sensor tersebut harus dilepas ketika anda berpindah activity agar tidak boros baterai.

Server yang terhubung pada aplikasi adalah server dengan spesifikasi:
  - Alamat Server   : 167.205.24.132
  - Port Server     : 3111

## Spesifikasi Tampilan
**Tampilan Home**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/a.jpg)
**Tampilan Peta**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/b.jpg)
**Tampilan Peta**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/c.jpg)
**Tampilan Intent Kamera**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/d.jpg)
**Tampilan Pilihan Jawaban**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/e.jpg)
**Tampilan Peta**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/f.jpg)
**Tampilan peta horizontal**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/g.jpg)
**Tampilan submit page**
![image](http://gitlab.informatika.org/mfikria/Tubes1-Android/raw/master/img/h.jpg)

## Lokasi File
~/bin : file apk (executable)
~/src : source code

