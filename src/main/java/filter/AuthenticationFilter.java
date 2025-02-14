package filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// các đường dẫn sẽ kích hoạt filter 
@WebFilter(filterName = "authenFilter", urlPatterns = { "/user", "/role", "/job", "/task" })
public class AuthenticationFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Khởi tạo filter nếu cần
		// Bạn có thể truy cập filterConfig để lấy các cấu hình, ví dụ: filterConfig.getInitParameter("someParameter")
		System.out.println("AuthenticationFilter initialized");
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String roleName = null;
		Cookie[] cookies = req.getCookies(); // Lấy tất cả cookies từ request

		// Kiểm tra xem cookies có null không
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				// Kiểm tra xem cookie có tên là "role" không
				if ("role".equals(cookie.getName())) {
					roleName = cookie.getValue(); // Lấy giá trị của cookie "role"
					break; // Không cần kiểm tra tiếp nếu đã tìm thấy cookie "role"
				}
			}
		}

		String path = req.getServletPath();
		// System.out.println(path);
		String context = req.getContextPath();
		if (roleName == null) {
			resp.sendRedirect(req.getContextPath() + "/index.html"); // Chuyển hướng nếu không có cookie "role"
		} else {
			switch (path) {
			case "/role":
				if (roleName.equals("ROLE_ADMIN")) {

					chain.doFilter(req, resp);
				} else {
					resp.sendRedirect(context + "/index.html");
				}
				break;
			case "/user":
				if (roleName.equals("ROLE_ADMIN") || roleName.equals("ROLE_MANAGER")) {

					chain.doFilter(req, resp);
				} else {
					resp.sendRedirect(context + "/index.html");
				}
				// chain.doFilter(req, resp);
				break;

			case "/job":
				if (roleName.equals("ROLE_ADMIN") || roleName.equals("ROLE_MANAGER")) {
					System.out.println(" filter job");
					chain.doFilter(req, resp);
				} else {
					resp.sendRedirect(context + "/index.html");
				}
				break;
			case "/task":
				if (roleName.equals("ROLE_ADMIN") || roleName.equals("ROLE_USER")) {
					chain.doFilter(req, resp);
				} else {
					resp.sendRedirect(context + "/index.html");
				}
				break;

			default:
				System.out.println(" filter chua dang nhap");
				resp.sendRedirect(context + "/index.html");
			}
		}
		System.out.println("Kiemtra filter");
		// cho phép đi tiếp, ko có dòng này là chặn
		// chain.doFilter(req, resp);
	}
	@Override
	public void destroy() {
		// Dọn dẹp tài nguyên, nếu cần (ví dụ: đóng kết nối, giải phóng tài nguyên)
	}
}
