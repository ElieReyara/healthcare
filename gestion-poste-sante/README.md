## 🤖 AI-Driven Development Showcase

Ce projet démontre une approche **Documentation-Driven Development** avec orchestration GitHub Copilot.

### Méthodologie
→ **Guardrails stricts** : `.github/copilot-instructions.md` définit les règles architecture  
→ **Context living doc** : `CONTEXT.md` maintient le modèle de données + roadmap  
→ **Audit trail** : `logs/LOG.md` trace chronologiquement chaque modification  
→ **Clean Architecture** : Séparation couches (UI, Service, Repository, Domain)

### Workflow orchestration
1. Mise à jour `CONTEXT.md` (module en cours)
2. Prompt structuré à Copilot (avec références fichiers)
3. Validation manuelle (compilation + tests)
4. Logging automatique dans `logs/LOG.md`
5. Commit Git après validation complète

### Résultat
✅ Code cohérent et maintenable  
✅ Architecture scalable  
✅ Documentation synchronisée avec le code  
✅ Traçabilité complète des décisions techniques

**Voir `.github/copilot-instructions.md` pour les détails d'implémentation.**
