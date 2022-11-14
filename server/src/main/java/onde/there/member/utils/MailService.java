package onde.there.member.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Member;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendSignupMail(String uuid, Member member) {
        String subject = "[tHere] 회원 가입 인증 메일입니다!";
        String text = "<h1>[이메일 인증]</h1> <p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p> " +
                "<a href='http://ec2-3-34-2-239.ap-northeast-2.compute.amazonaws.com:8080/members/signup/confirm?key="+uuid+"' target='_blenk'>이메일 인증 확인</a>";

        MimeMessagePreparator mail = (mimeMessage) -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(member.getEmail());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
        };

        javaMailSender.send(mail);
    }
}
