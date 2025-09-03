export const formatDate = (arr) => {
    if (!Array.isArray(arr)) return "Không xác định";
    const [y, m, d, h, min, s] = arr;
    return new Date(y, m - 1, d, h, min, s).toLocaleString("vi-VN");
};