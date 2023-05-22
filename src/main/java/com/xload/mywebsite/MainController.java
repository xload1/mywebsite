package com.xload.mywebsite;

import com.xload.mywebsite.dbconn.User;
import com.xload.mywebsite.dbconn.UserHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    UserHelper userHelper;
    String registerError = "";
    String loginError = "";

    @Autowired
    public MainController(UserHelper userHelper) {
        this.userHelper = userHelper;
    }

    @GetMapping("/")
    public String redirect() {
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String main(HttpServletRequest request, Model model) {
        model.addAttribute("login", getLogin(request));
        return "main";
    }

    @GetMapping("/list")
    public String list(Model model, HttpServletRequest request) {
        model.addAttribute("users", userHelper.getUsers());
        model.addAttribute("login", getLogin(request));
        if (userHelper.getUser(getLogin(request)) != null) {
            model.addAttribute("likedUserLogin", userHelper.getUser(getLogin(request)).getLiked());
        }
        return "list";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("login", getLogin(request));
        model.addAttribute("loginError", loginError);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request) {
        model.addAttribute("login", getLogin(request));
        model.addAttribute("registerError", registerError);
        return "register";
    }

    @PostMapping("/register/check")
    public String registerCheck(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {

        if (userHelper.getUser(username) == null) {
            if (password.length() >= 8) {
                if (username.matches("[a-z0-9]+")) {
                    if (username.length() < 20) {
                        if (password.length() < 25) {
                            userHelper.addUser(new User(username, password));
                            registerError = "successfully registered";
                            Cookie loginCookie = new Cookie("login", username);
                            loginCookie.setMaxAge(30 * 60);
                            loginCookie.setPath("/");
                            response.addCookie(loginCookie);
                            return "redirect:/register";
                        } else {
                            registerError = "Password must be less than 25 characters long";
                        }
                    } else {
                        registerError = "Username must be less than 20 characters long";
                    }
                } else {
                    registerError = "Username must contain only lower letters and numbers";
                }
            } else {
                registerError = "Password must be at least 8 characters long";
            }
        } else {
            registerError = "User already exists";
        }
        return "redirect:/register";
    }

    @PostMapping("/login/check")
    public String loginCheck(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        User user = userHelper.getUser(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                loginError = "successfully logged in";
                Cookie loginCookie = new Cookie("login", username);
                loginCookie.setMaxAge(30 * 60);
                loginCookie.setPath("/");
                response.addCookie(loginCookie);
                return "redirect:/login";
            } else {
                loginError = "Wrong password";
            }
        } else {
            loginError = "User does not exist";
        }
        return "redirect:/login";
    }

    public String getLogin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("login")) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    @PostMapping("/list/like")
    public String like(@RequestParam String login, HttpServletRequest request) {
        User likedUser = userHelper.getUser(login);
        if (!getLogin(request).equals("")) {
            User currentUser = userHelper.getUser(getLogin(request));
            if (!currentUser.getLogin().equals(likedUser.getLogin())) {
                if (currentUser.getLiked() != null) {
                    User oldUser = userHelper.getUser(currentUser.getLiked());
                    oldUser.setLikes(oldUser.getLikes() - 1);
                    userHelper.saveUser(oldUser);
                    likedUser.setLikes(likedUser.getLikes() + 1);
                    userHelper.saveUser(likedUser);
                    currentUser.setLiked(likedUser.getLogin());
                    userHelper.saveUser(currentUser);
                } else {
                    likedUser.setLikes(likedUser.getLikes() + 1);
                    userHelper.saveUser(likedUser);
                    currentUser.setLiked(likedUser.getLogin());
                    userHelper.saveUser(currentUser);
                }
            }
        }
        return "redirect:/list";
    }

    @GetMapping("/list/{login}")
    public String user(@PathVariable String login, Model model, HttpServletRequest request) {
        if(login.equals("")) return "redirect:/main";
        model.addAttribute("user", userHelper.getUser(login));
        if (login.equals(getLogin(request))) {
            return "user";
        } else {
            return "users";
        }
    }
    @PostMapping("/edit-text")
    public String editText(@RequestParam String text, HttpServletRequest request) {
        User user = userHelper.getUser(getLogin(request));
        user.setText(text);
        userHelper.saveUser(user);
        return "redirect:/list/"+user.getLogin();
    }
}
