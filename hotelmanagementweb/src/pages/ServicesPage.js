import { motion } from "framer-motion";
import { Bed, Utensils, Dumbbell, Wifi, Car, Coffee } from "lucide-react";

const ServicesPage = () => {
    const services = [
        {
            title: "Phòng nghỉ tiện nghi",
            desc: "Các loại phòng đa dạng, từ tiêu chuẩn đến cao cấp, đầy đủ tiện nghi, dọn dẹp hằng ngày.",
            icon: <Bed className="w-8 h-8 text-indigo-500" />,
            image: "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80",
        },
        {
            title: "Nhà hàng & Ẩm thực",
            desc: "Thưởng thức món ăn đa dạng từ Âu đến Á, thực đơn thay đổi mỗi ngày, phục vụ tại phòng.",
            icon: <Utensils className="w-8 h-8 text-indigo-500" />,
            image: "https://images.unsplash.com/photo-1600891964092-4316c288032e?auto=format&fit=crop&w=800&q=80",
        },
        {
            title: "Phòng Gym & Spa",
            desc: "Giữ dáng và thư giãn với phòng gym hiện đại, spa, xông hơi, và dịch vụ massage chuyên nghiệp.",
            icon: <Dumbbell className="w-8 h-8 text-indigo-500" />,
            image: "https://images.unsplash.com/photo-1576678927484-cc907957088c?auto=format&fit=crop&w=800&q=80",
        },
        {
            title: "Wifi tốc độ cao",
            desc: "Miễn phí wifi tốc độ cao trong toàn bộ khu vực khách sạn để bạn luôn kết nối mọi lúc.",
            icon: <Wifi className="w-8 h-8 text-indigo-500" />,
            image: "https://images.unsplash.com/photo-1516044734145-07ca8eef8731?auto=format&fit=crop&w=800&q=80",
        },
        {
            title: "Dịch vụ đưa đón",
            desc: "Hỗ trợ đặt xe đưa đón sân bay, taxi, thuê xe riêng theo yêu cầu để bạn di chuyển dễ dàng.",
            icon: <Car className="w-8 h-8 text-indigo-500" />,
            image: "https://images.unsplash.com/photo-1695357605057-059efb3d1b71?auto=format&fit=crop&w=800&q=80",
        },
        {
            title: "Quầy cafe & Lounge",
            desc: "Không gian cafe thư giãn, phục vụ đồ uống, cocktail, nơi gặp gỡ lý tưởng cùng bạn bè.",
            icon: <Coffee className="w-8 h-8 text-indigo-500" />,
            image: "https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=800&q=80",
        },
    ];

    return (
        <div className="min-h-screen bg-gradient-to-br from-indigo-50 via-white to-cyan-50 dark:from-gray-900 dark:via-gray-950 dark:to-gray-900 px-6 py-12 transition-colors duration-300">
            <div className="max-w-6xl mx-auto text-center space-y-12">
                <motion.h1
                    initial={{ opacity: 0, y: -20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.6 }}
                    className="text-4xl md:text-5xl font-extrabold text-gray-900 dark:text-white"
                >
                    Dịch vụ của chúng tôi
                </motion.h1>

                <motion.p
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.2, duration: 0.6 }}
                    className="max-w-3xl mx-auto text-gray-700 dark:text-gray-300"
                >
                    Chúng tôi cung cấp đầy đủ các dịch vụ nhằm mang đến cho bạn trải nghiệm lưu trú trọn vẹn: tiện nghi, thoải mái và đáng nhớ.
                </motion.p>

                <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-8 mt-10">
                    {services.map((service, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ delay: 0.3 + i * 0.1 }}
                            className="bg-white dark:bg-gray-800 shadow-md rounded-2xl overflow-hidden border border-gray-200 dark:border-gray-700 hover:shadow-xl hover:scale-[1.02] transition-transform"
                        >
                            <img
                                src={service.image}
                                alt={service.title}
                                className="h-40 w-full object-cover"
                            />
                            <div className="p-6 flex flex-col items-center text-center">
                                {service.icon}
                                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mt-4">
                                    {service.title}
                                </h3>
                                <p className="text-gray-600 dark:text-gray-400 text-sm mt-2">
                                    {service.desc}
                                </p>
                            </div>
                        </motion.div>
                    ))}
                </div>

                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ delay: 0.5 }}
                    className="mt-12"
                >
                    <a
                        href="/search"
                        className="px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white text-lg font-semibold rounded-full shadow-lg transition-colors"
                    >
                        Khám phá & đặt phòng ngay
                    </a>
                </motion.div>
            </div>
        </div>
    );
};

export default ServicesPage;
