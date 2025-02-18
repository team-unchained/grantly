grep -q "127.0.0.1 grantly.work" /etc/hosts || echo -e "\n127.0.0.1 grantly.work" | sudo tee -a /etc/hosts
