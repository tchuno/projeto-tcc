package net.gnfe.util.other;

import io.jsonwebtoken.*;
import net.gnfe.bin.domain.entity.Usuario;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginJwtUtils {

    private static final String LOGIN_JWT = "JWTGNFE";
    private static final long TEMPO_VALIDADE_TOKEN_HORAS = 16L;

    private static final String CHAVE = "fGoZ=20whcM8iZjEu7wcKPynKlhroL2hKd3MWs93LL2tlbzWYkzi1mMCNut26maHGSUdBTWMgN7IpxfDXTmO6\n";

    /** O nome também deve ser alterado para o cliente correspondente ao projeto. */
    private static String CLIENTE = "GNFE";

    public static void criarCookie(Usuario usuarioLogando, HttpServletResponse response) {

        String login = usuarioLogando.getLogin();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dataExpiracao = now.plusHours(TEMPO_VALIDADE_TOKEN_HORAS);

        Date dataExpiracaoDate = Date.from(
                dataExpiracao
                        .atZone(ZoneId.systemDefault())
                        .toInstant());

        Duration duration = Duration.between(now, dataExpiracao);
        long expiracaoSegundos = duration.toMillis() / 1000;

        String emitente = Usuario.server;
        String chave = getChave();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), signatureAlgorithm.getJcaName());

        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(emitente)
                .setIssuedAt(new Date())
                .setExpiration(dataExpiracaoDate)
                .signWith(signingKey, signatureAlgorithm)
                .compact();

        Cookie cookie = new Cookie(LOGIN_JWT + CLIENTE, jwtToken);
        cookie.setPath("/");
        cookie.setMaxAge((int) expiracaoSegundos);
        response.addCookie(cookie);
    }

    private static String getChave() {
        String emitente = Usuario.server;
        return CHAVE + "_" + CLIENTE + "_" + emitente;
    }

    public static boolean checkLogadoCookie(HttpServletRequest request, String login) {
        String loginCookie = checkLogadoCookie(request);
        if(StringUtils.isBlank(loginCookie)) {
            return false;
        }
        return login.equals(loginCookie);
    }

    public static String checkLogadoCookie(HttpServletRequest request) {

        List<String> cookiesValues = getCookiesValues(request, LOGIN_JWT + CLIENTE);
        if(cookiesValues == null || cookiesValues.isEmpty()) {
            return null;
        }

        String jwtToken = cookiesValues.get(0);
        if("none".equals(jwtToken)) {
            return null;
        }

        String chave = getChave();
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = new SecretKeySpec(chave.getBytes(StandardCharsets.UTF_8), signatureAlgorithm.getJcaName());

        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(jwtToken);

            Claims body = claimsJws.getBody();

            Date expiration = body.getExpiration();
            boolean expirado = expiration.before(new Date());
            if(expirado) {
                return null;
            }

            String login = body.getSubject();

            return login;
        }
        catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<String> getCookiesValues(HttpServletRequest request, String name) {

        List<String> list = new ArrayList<>();
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if(name.equals(cookieName)) {
                    String cookieValue = cookie.getValue();
                    list.add(cookieValue);
                }
            }
        }

        return list;
    }

    public static void invalidarCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(LOGIN_JWT + CLIENTE, "none");
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
