import { Hotel, CreditCard, CalendarCheck } from "lucide-react"
import { useNavigate, Link } from "react-router-dom";
import { useUserContext } from "../configs/UserContext";

const HomePage = () => {
    const navigate = useNavigate();
    const { state } = useUserContext();
    const user = state?.currentUser;

    return (
        <>
            <section className="relative bg-gradient-to-r from-indigo-900 via-slate-900 to-cyan-900 text-white py-24 text-center flex flex-col items-center justify-center">
                <div className="relative z-10">
                    {user ? (
                        <>
                            <h1 className="text-4xl md:text-5xl font-bold mb-4">
                                Xin chào <span className="text-indigo-600">{user.firstName || user.username}</span> 👋
                            </h1>
                            <p className="text-slate-600 dark:text-slate-300 max-w-2xl mb-8">
                                Chúc bạn một ngày tốt lành! Quản lý đặt phòng và trải nghiệm dịch vụ khách sạn tiện lợi.
                            </p>
                            <div className="flex gap-4 justify-center">
                                <button
                                    onClick={() => navigate("/profile")}
                                    className="rounded-xl border border-slate-300 px-6 py-3 font-semibold text-slate-900 bg-slate-600 
                            hover:bg-slate-100 dark:border-slate-700 dark:text-slate-100 dark:hover:bg-slate-800"
                                >
                                    Hồ sơ cá nhân
                                </button>
                            </div>
                        </>
                    ) : (
                        <>
                            <h1 className="text-4xl md:text-5xl font-bold mb-4">
                                Trải nghiệm <span className="text-indigo-600">khách sạn thông minh</span>
                            </h1>
                            <p className="text-slate-600 dark:text-slate-300 max-w-2xl mb-8">
                                Đặt phòng nhanh chóng, thanh toán tiện lợi, quản lý thông minh – tất cả trong một nền tảng.
                            </p>
                            <div className="flex gap-4 justify-center">
                                <button
                                    onClick={() => navigate("/signup")}
                                    className="rounded-xl bg-indigo-600 px-6 py-3 font-semibold text-white hover:bg-indigo-700"
                                >
                                    Bắt đầu ngay
                                </button>
                                <button
                                    onClick={() => navigate("/login")}
                                    className="rounded-xl border border-slate-300 px-6 py-3 font-semibold text-slate-900 
                            hover:bg-slate-100 dark:border-slate-700 dark:text-slate-100 dark:hover:bg-slate-800"
                                >
                                    Đăng nhập
                                </button>
                            </div>
                        </>
                    )}
                </div>
            </section>

            <section className="max-w-6xl mx-auto px-6 py-16 grid md:grid-cols-3 gap-10">
                <div className="p-6 rounded-xl shadow bg-white dark:bg-slate-900">
                    <h3 className="flex items-center gap-2 font-semibold text-lg mb-2">
                        <Hotel className="text-indigo-600" size={22} /> Đặt phòng nhanh
                    </h3>
                    <p className="text-slate-600 dark:text-slate-400">
                        Tìm kiếm & đặt phòng chỉ trong vài giây với giao diện trực quan.
                    </p>
                </div>
                <div className="p-6 rounded-xl shadow bg-white dark:bg-slate-900">
                    <h3 className="flex items-center gap-2 font-semibold text-lg mb-2">
                        <CreditCard className="text-indigo-600" size={22} /> Thanh toán tiện lợi
                    </h3>
                    <p className="text-slate-600 dark:text-slate-400">
                        Hỗ trợ nhiều phương thức thanh toán an toàn và nhanh chóng.
                    </p>
                </div>
                <div className="p-6 rounded-xl shadow bg-white dark:bg-slate-900">
                    <h3 className="flex items-center gap-2 font-semibold text-lg mb-2">
                        <CalendarCheck className="text-indigo-600" size={22} /> Quản lý thông minh
                    </h3>
                    <p className="text-slate-600 dark:text-slate-400">
                        Theo dõi lịch đặt, tiến độ lưu trú và thông tin cá nhân dễ dàng.
                    </p>
                </div>
            </section>

            {/* Featured Rooms */}
            <section className="bg-slate-50 dark:bg-slate-900 py-16 px-6">
                <div className="max-w-6xl mx-auto text-center mb-10">
                    <h2 className="text-2xl md:text-3xl font-bold dark:text-white">Phòng nổi bật</h2>
                    <p className="text-slate-600 dark:text-slate-400 mt-2">
                        Khám phá các lựa chọn phòng được yêu thích nhất
                    </p>
                </div>
                <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
                    {[1, 2, 3].map((room) => (
                        <div key={room} className="rounded-xl overflow-hidden shadow bg-white dark:bg-slate-800">
                            <div className="h-40 bg-gray-200 dark:bg-gray-700"></div>
                            <div className="p-4">
                                <h3 className="font-semibold text-lg">Phòng Deluxe {room}</h3>
                                <p className="text-slate-600 dark:text-slate-400 text-sm mb-2">Giá từ 1,200,000 VND/đêm</p>
                                <Link
                                    to={`/rooms/${room}`}
                                    className="text-indigo-600 hover:underline dark:text-indigo-400"
                                >
                                    Xem chi tiết →
                                </Link>
                            </div>
                        </div>
                    ))}
                </div>
            </section>

            {/* Feedbacks Section */}
            <section className="max-w-6xl mx-auto px-6 py-16">
                <h2 className="text-2xl md:text-3xl font-bold text-center dark:text-white mb-10">
                    Khách hàng nói gì?
                </h2>
                <div className="grid md:grid-cols-3 gap-8">
                    {[
                        { name: "Anh Minh", text: "Dịch vụ tuyệt vời, nhân viên thân thiện và chuyên nghiệp." },
                        { name: "Chị Hằng", text: "Phòng sạch sẽ, tiện nghi đầy đủ. Tôi sẽ quay lại lần sau!" },
                        { name: "Bạn Nam", text: "Ứng dụng dễ dùng, đặt phòng nhanh chóng." }
                    ].map((fb, i) => (
                        <div key={i} className="p-6 rounded-xl shadow bg-white dark:bg-slate-900">
                            <p className="text-slate-600 dark:text-slate-300 italic mb-4">“{fb.text}”</p>
                            <p className="font-semibold dark:text-white">- {fb.name}</p>
                        </div>
                    ))}
                </div>
            </section>

            <section className="text-center py-16 bg-indigo-400 text-white">
                <h2 className="text-3xl font-bold mb-4">Sẵn sàng trải nghiệm?</h2>
                <p className="mb-6">Đặt phòng ngay để nhận ưu đãi đặc biệt hôm nay</p>
                <button
                    onClick={() => navigate("/rooms")}
                    className="px-6 py-3 rounded-xl bg-white text-indigo-600 font-semibold hover:bg-slate-100"
                >
                    Khám phá phòng
                </button>
            </section>
        </>
    );
};

export default HomePage;
