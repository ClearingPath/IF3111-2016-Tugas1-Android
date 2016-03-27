# Tugas Besar 1 IF3111 Pengembangan Aplikasi pada Platform Khusus

## Nama : Vincent Theophilus Ciputra

## NIM : 13513005

## Latar Belakang

Dalam tugas ini, saya diminta untuk menemukan beberapa tempat di lingkungan ITB dengan bantuan tools yang dibangun di atas platform Android. Aplikasi tersebut dapat memandu saya dengan menampilkan peta (Google Maps) dan arah tujuannya. Saya diminta menemukan 3 lokasi (akan diberikan dari server). Pada setiap lokasi peserta akan diminta untuk foto-diri di lokasinya dan mengirimkan nama lokasi tempat peserta berada. 

Daftar kemungkinan jawaban lokasi diberikan oleh asisten. 

Daftar kemungkinan jawaban lokasi adalah

* gku_barat
* gku_timur
* intel
* cc_barat
* cc_timur
* dpr
* sunken
* perpustakaan
* pau
* kubus

## Spesifikasi Aplikasi

Spesifikasi dari aplikasi yang dibangun sebagai berikut

* Aplikasi mampu menerima pesan dari server dengan format JSON berisi lokasi dan token.
* Aplikasi mampu mengolah data berupa location point (longitude, latitude) dan menampilkan indicator pada peta lokasi yang dimaksud. Peta lokasi menggunakan Google Map API. (Tampilan silahkan lihat Spesifikasi Tampilan).
* Terdapat sebuah panah navigasi yang berada diatas peta (letak bebas), yang menunjukan arah utara. Anda diminta menggunakan sensor yang ada pada android API (Tampilan silahkan lihat Spesifikasi Tampilan).
* Aplikasi mampu mengirim intent kamera.
* Aplikasi mampu mengambil gambar melalui kamera. Gambar tidak perlu diunggah ke server (silakan lihat spesifikasi tanya jawab asisten)
* Aplikasi mampu mengirimkan pesan ke sever dengan format JSON berisi lokasi (longitude, latitude), nim serta token.
* Log tersimpan dalam file


## Spesifikasi Pertukaran Pesan
Keterangan : *Client* pada dokumen ini adalah aplikasi ini dan *Server* merupakan server milik asisten.

### Request Location
Permintaan lokasi (pertama).

**Client Request**
```sh
{“com”:”req_loc”,”nim”:”13512999”}
```
**Server Response** 
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
### Send Answer
Mengirimkan jawaban dan menerima lokasi berikutnya

**Client Request**
```sh
{“com”:”answer”,”nim”:”13512999”,”answer”:”labtek_v”, ”longitude”:”6.234123132”,”latitude”:”0.1234123412”,”token”:”21nu2f2n3rh23diefef23hr23ew”}
```
**Server Response**
Jika jawaban Anda **benar**, maka:
```sh
{“status”:”ok”,”nim”:”13512999”,”longitude”:”8.13215123214”,”latitude”:”9.1234123412”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **salah**, maka:
```sh
{“status”:”wrong_answer”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”}
```
Jika jawaban Anda **benar dan sudah berada dilokasi ketiga**, maka:
```sh
{“status”:”finish”,”nim”:”13512999”,”token”:”124fewfm32r32ifmwder42”,”check”:1}
```
## Spesifikasi Tampilan
**Tampilan portrait**
![enter image description here](https://photos-5.dropbox.com/t/2/AAAvUXopB4ZelqlS8LFLy1AV4bxDpMC_SkDfcCwVENnLMg/12/343631524/png/32x32/3/1459108800/0/2/portrait.png/EPjW9NcCGAwgASgB/uKa317g6nMsiVNGK00rN9--FUgGMD2g9M_m7DMp3ojw?size_mode=3&size=800x600)

![enter image description here](https://photos-4.dropbox.com/t/2/AACmkgSrSif45QMu5Re9dXisQGK6sa11VjsfzSsrb1DmYA/12/343631524/png/32x32/1/_/1/2/portrait2.png/EPjW9NcCGAwgASgB/jYmRqCjgeDdCX3y5aT8UaClH9zt9hKsAzfmb9tbVHDI?size=1024x768&size_mode=3)

![enter image description here](https://photos-4.dropbox.com/t/2/AAB7YNhr2T9PNbgIPnY1aRnGOZgHX5gIjkfb8HLSh-ggsQ/12/343631524/png/32x32/1/_/1/2/portrait3.png/EPjW9NcCGAwgASgB/kWTzQsAX6LSGDz9aoMy9kPQBZSbB6LoXrU9VBT5ThIE?size=1024x768&size_mode=3)

**Tampilan vertikal**
![enter image description here](https://photos-6.dropbox.com/t/2/AACLkJQ_j_yeYSnV_eRDQPm3BznRgPOP_E1nD7DUn_Ztaw/12/343631524/png/32x32/1/_/1/2/landscape.png/EPjW9NcCGAwgASgB/6CxN_YofdC7FVkjEbIsDBgse2v7Mwi0_PxndOlK0kdo?size=1024x768&size_mode=3)

![enter image description here](https://photos-6.dropbox.com/t/2/AAA8Ii06RI0v90gGq59_qWaXCQq4aLbUh4-iAs_Rz8XNdA/12/343631524/png/32x32/1/_/1/2/landscape2.png/EPjW9NcCGAwgASgB/8EAYsIjWHvLT95YZT4MsuFOSX2KiiuQk1Jcgk9zDyB4?size=1024x768&size_mode=3)

**Tampilan *submit* jawaban**
![enter image description here](https://photos-2.dropbox.com/t/2/AABUf-bjZUDAvIF8xz6u_pW-iyvrcgck0MepheEdent3TQ/12/343631524/png/32x32/1/_/1/2/submit.png/EPjW9NcCGAwgASgB/P-gxUdugSKcj0-nNRr5vGnasI4qPna41BMQYl1479fY?size=1024x768&size_mode=3)

**Tampilan log file**
![enter image description here](https://photos-5.dropbox.com/t/2/AAAKTH1v4AB8K4pn1UH3CHiCQSfn4LKA6U3C_H2rXbSe0w/12/343631524/png/32x32/1/_/1/2/log.png/EPjW9NcCGAwgASgB/iN_RBUHJUJKJEn2Z6iaqJ-s4yFcEFyNdDJ3IVTkDe4A?size=1024x768&size_mode=3)

## Deliverables

Silahkan ikuti langkah pengumpulan berikut :

- Lakukan **fork** terhadap repository ini.
- Edit file readme ini semenarik mungkin (gunakan panduan [Markdown] langguage), diperbolehkan untuk merubah struktur dari readme ini. (Soal tidak perlu dipertahankan).
- Pada Readme terdapat tampilan aplikasi.
- Cantumkan lokasi *source code* dan *binary* dari aplikasi pada Readme.




## Lokasi file
Lokasi source code : GoogleMap\app\src\main\java\com\example\user\googlemap

Lokasi res : GoogleMap\app\src\main\res

Lokasi binary : GoogleMap\app

[Markdown]: <http://dillinger.io/>