FROM gitlab/gitlab-ce:8.13.5-ce.0

RUN echo "nginx['listen_port'] = 80" >> /etc/gitlab/gitlab.rb \
    && echo "nginx['listen_https'] = false" >> /etc/gitlab/gitlab.rb \
    && echo "nginx['proxy_set_headers'] = {" >> /etc/gitlab/gitlab.rb \
    && echo '  "X-Forwarded-Proto" => "https",' >> /etc/gitlab.rb \
    && echo '  "X-Forwarded-Ssl" => "on"' >> /etc/gitlab.rb \
    && echo '}' >> /etc/gitlab.rb 
