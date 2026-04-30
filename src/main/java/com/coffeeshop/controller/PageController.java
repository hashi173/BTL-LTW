package com.coffeeshop.controller;

import com.coffeeshop.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PageController {

    private final JobApplicationRepository jobApplicationRepository;
    private final MessageSource messageSource;

    @GetMapping("/about")
    public String about() {
        return "redirect:/#about";
    }

    @GetMapping("/active")
    public String active() {
        return "redirect:/";
    }

    @GetMapping("/careers")
    public String careers() {
        return "redirect:/#careers";
    }

    @GetMapping("/info")
    public String info() {
        return "redirect:/#info";
    }

    @org.springframework.web.bind.annotation.PostMapping("/careers/apply")
    public String applyForJob(
            @org.springframework.web.bind.annotation.RequestParam("fullName") String fullName,
            @org.springframework.web.bind.annotation.RequestParam("email") String email,
            @org.springframework.web.bind.annotation.RequestParam("phone") String phone,
            @org.springframework.web.bind.annotation.RequestParam("position") String position,
            @org.springframework.web.bind.annotation.RequestParam("cvFile") org.springframework.web.multipart.MultipartFile cvFile,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
            java.util.Locale locale) {

        try {
            com.coffeeshop.entity.JobApplication application = new com.coffeeshop.entity.JobApplication();
            application.setFullName(fullName);
            application.setEmail(email);
            application.setPhone(phone);
            application.setPosition(position);

            if (!cvFile.isEmpty()) {
                String originalFileName = StringUtils.cleanPath(
                        java.util.Optional.ofNullable(cvFile.getOriginalFilename()).orElse("cv.pdf"));
                String safeFileName = java.nio.file.Paths.get(originalFileName).getFileName().toString();
                String fileName = java.util.UUID.randomUUID() + "_" + safeFileName;
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads/cv");
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                java.nio.file.Files.copy(cvFile.getInputStream(), uploadPath.resolve(fileName),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                application.setCvUrl("/uploads/cv/" + fileName);
            }

            String trackingCode = "CV-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            application.setTrackingCode(trackingCode);

            jobApplicationRepository.save(application);
            redirectAttributes.addFlashAttribute("success",
                    messageSource.getMessage("careers.apply.success", null, locale));
            redirectAttributes.addFlashAttribute("trackingCode", trackingCode);
        } catch (Exception exception) {
            log.error("Failed to save job application for {}", email, exception);
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("careers.apply.error", null, locale));
        }

        return "redirect:/#careers";
    }

    @org.springframework.web.bind.annotation.PostMapping("/contact")
    public String contact(org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
            java.util.Locale locale) {
        redirectAttributes.addFlashAttribute("success",
                messageSource.getMessage("contact.success", null, locale));
        return "redirect:/#info";
    }
}
