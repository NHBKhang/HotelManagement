import SearchRooms from "./SearchRooms";

const SearchHero = () => {
    return (
        <section className="relative bg-gradient-to-r from-indigo-900 via-slate-900 to-cyan-900 text-white py-24">
            <div className="absolute inset-0">
                <img
                    src="./img/hotel-bg.webp"
                    alt="Luxury Hotel"
                    className="h-full w-full object-cover opacity-40"
                />
            </div>

            <div className="relative max-w-7xl mx-auto px-6 text-center">
                <h1 className="text-4xl md:text-6xl font-extrabold mb-6 leading-tight">
                    Trải nghiệm <span className="text-indigo-300">khách sạn thông minh</span>
                </h1>
                <p className="text-lg md:text-xl text-slate-200 mb-10 max-w-3xl mx-auto">
                    Tìm phòng, đặt dịch vụ và thanh toán an toàn — tất cả trong một nền tảng hiện đại.
                </p>

                <div className="mt-6 bg-white/90 backdrop-blur-md rounded-2xl shadow-xl p-6 max-w-4xl mx-auto">
                    <SearchRooms />
                </div>
            </div>
        </section>
    );
}

export default SearchHero;