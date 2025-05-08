grep -q "127.0.0.1 grantly.work" /etc/hosts || echo "\n127.0.0.1 grantly.work" | sudo tee -a /etc/hosts
grep -q "127.0.0.1 api.grantly.work" /etc/hosts || echo "\n127.0.0.1 api.grantly.work" | sudo tee -a /etc/hosts
grep -q "127.0.0.1 grafana.grantly.work" /etc/hosts || echo "\n127.0.0.1 grafana.grantly.work" | sudo tee -a /etc/hosts