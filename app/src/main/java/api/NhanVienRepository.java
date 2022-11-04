package api;

public class NhanVienRepository {
    public static NhanVienService getNhanvienService() {
        return APIClinet.getClient().create(NhanVienService.class);
    }
}
