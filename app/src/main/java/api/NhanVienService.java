package api;

import model.NhanVien;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NhanVienService {
    String NHANVIEN = "Nhanvien";    // "Nhanvien" is table name in API

    @GET(NHANVIEN)
    Call<NhanVien[]> getAllNhanviens();

    @GET(NHANVIEN + "/{id}")
    Call<NhanVien> getNhanvienById(@Path("id") Object id);

    @POST(NHANVIEN)
    Call<NhanVien> createNhanvien(@Body NhanVien nhanvien);

    @PUT(NHANVIEN + "/{id}")
    Call<NhanVien> updateNhanvien(@Path("id") Object id, @Body NhanVien nhanvien);

    @DELETE(NHANVIEN + "/{id}")
    Call<NhanVien> deleteNhanvienById(@Path("id") Object id);
}
