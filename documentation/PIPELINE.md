# Secure communication between Jenkins and Gitlab

## Configure port redirections in Gitlab

By default, Gitlab listens on ports 80 and 22 for http and ssh communication.

When running Gitlab inside a container, it is useful to map those ports to higher numbers to avoid conflicts.
However, Gitlab must be informed of the redirections.
One way to do this is to add directives to the Gitlab Omnibus configuration by setting the `GITLAB_OMNIBUS_CONFIG`.

Example in docker-compose.yml

```yaml
version: '2'
services:
  gitlab:
    image: 'gitlab/gitlab-ce:latest'
    restart: always
    environment:
      GITLAB_OMNIBUS_CONFIG: |
        external_url 'http://192.168.99.100:8000'
        gitlab_rails['gitlab_shell_ssh_port'] = 2200
    ports:
      - '8000:8000'
      - '4430:443'
      - '2200:22'
```

## Use SSH key authentication

There seems to be a bug in the Jenkins Git plugin when using Basic authentication.

The work around is to:
- generate an SSH key pair inside the Jenkins container;
- configure the credentials for the Pipeline project using the private key;
- register the public key with the account used on Gitlab.
