# Projeto

Nesse projeto foi desenvolvido um Dockerfile que executa uma aplicação Java 11 e uma Pipeline scriptada em Groovy com orquestrador Jenkins. 


                

# Dockerfile

Este Dockerfile cria uma imagem para executar um projeto Java com suporte a monitoramento Prometheus.

Foi utilizado a imagem base "openjdk:11jre-slim".

# Configurações e Ferramentas utilizadas

- Timezone - Define o fuso horário para America/Sao-Paulo.

- Prometheus - Monitoramento de métricas.

- Pacotes adicionais - Instalação dos pacotes adicionais - curl, wget, gpg e net-tools.

- Sysdig - Ferramenta para troubleshooting, monitoramento e segurança.

- Certificado SSL - Adiciona um certificado SSL para conexão segura ao banco de dados.

- Diretório de Trabalho - Define o diretório de trabalho para /app

- Portas Expostas - Porta 8080 é exposta na rede do container.

- Cópia do JAR - Copia o arquivo JAR do aplicativo para o diretório /app

- Executando o Aplicativo - Executa o aplicaivo através da linha de comando java -jar app.jar.

- Comandos para construir a imagem

- Para construir a imagem, execute o comando:
- bash 
    - docker run -d -p 8080:8080 zallpy-java:11 .

Este comando inicia o aplicativo Java com o agente Prometheus e a configuração específica.

# Configuração Adicional com SSL

Se a aplicação Java necessita de uma conexão segura ao banco de dados com o certificado SSL, a variável de ambiente é configurada: 

JAVA_OPTS="-Djavax.net.ssl.trustStore=/app/database-cert.pem -Djavax.net.ssl.trustStorePassword=Zallpy"






# Pipeline

Este é um exemplo de script de pipeline Jenkins escrito em Groovy para automatizar o processo de construção, análise de código, teste, e escaneamento de imagem Docker para um projeto Java.

O pipeline utiliza o Maven para construção, o SonarQube para análise estática de código, e o Anchore para análise de vulnerabilidades na imagem Docker, Jenkins como ferramenta de CI/CD.

# Pré-requisitos

- Jenkins
- Maven
- SonarQube
- Achore

# Configurações

Oriento configurar corretamente as variáveis de ambiente e credenciais no Jenkins para garantir o funcionamento adequado do pipeline. Algumas das variáveis utilizadas são:

- project_name - Nome do projeto.
- path_to_pom - Caminho para o arquivo "pom.xml"
- git_repository - URL do repositório Git. 
- git_branch - Branch do Git a ser utilizado.
- git_credentials - Credencial do Git.

# Pipeline Steps

## Configuração do Ambiente Java

- Instalação e configuração do JDK 11.

## Obtenção do Código Fonte

- Clona o repositório Git usando as credenciais fornecidas.

## Construção do Artefato

- Move os arquivos fonte e realiza a construção do projeto Java com o Maven.
- Determina a versão do projeto a partir do arquivo "pom.xml"
- Define metas do Maven com base na versão 

## SonarQube Scanner

- Aguarda a conclusão do Quality Gate no SonarQube.
- Usa a instalação do SonarQube configurada no Jenkins.

## SonarQube Quality Gate

- Aguarda a conclusão do Quality Gate no SonarQube.
- Se falhar, a pipeline é interrompida.

## Anchore Scan

- Utiliza o Anchore para escanear a imagem Docker gerada.
- As credenciais e informações da imagem são fornecidas como variáveis.

# Recomendações

- Certifique-se de ter todas as ferramentas e serviços necessários instalados e configurados.
- Configure as variáveis de ambiente e credenciais no Jenkins conforme necessário.


# Autor

William Lino Lopes Gomes

# Versão da Imagem

A versão da imagem é 1.0. 
