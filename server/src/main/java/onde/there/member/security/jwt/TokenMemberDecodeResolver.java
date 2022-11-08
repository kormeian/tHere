package onde.there.member.security.jwt;

import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Member;
import onde.there.member.exception.type.MemberErrorCode;
import onde.there.member.exception.MemberException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class TokenMemberDecodeResolver implements HandlerMethodArgumentResolver {

    private final String ANONYMOUS_USER = "anonymousUser";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isTokenMemberEmail = parameter
                .getParameterAnnotation(TokenMemberId.class) != null;

        boolean isString = String.class.equals(parameter.getParameterType());

        return isTokenMemberEmail && isString;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal().equals(ANONYMOUS_USER)) {
            return null;
        }
        Member member = (Member) authentication.getPrincipal();
        return member.getId();
    }
}
