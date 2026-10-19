async function generateGeminiRoadmap(currentSkill, goal, experience, apiKey) {
    const prompt = `Create a practical, personalized learning roadmap for a ${experience} learner. They currently know "${currentSkill}" and want to learn "${goal}". Return a JSON object with a concise "title", a helpful "description", and a "steps" array. Each step must have a "title", a useful "description", and "completed": false.`;
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 30000);

    try {
        const response = await fetch(
            'https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'x-goog-api-key': apiKey
                },
                body: JSON.stringify({
                    contents: [{ parts: [{ text: prompt }] }],
                    generationConfig: { responseMimeType: 'application/json' }
                }),
                signal: controller.signal
            }
        );

        if (!response.ok) {
            throw new Error(`Gemini API returned HTTP ${response.status}`);
        }

        const result = await response.json();
        const content = result.candidates?.[0]?.content?.parts
            ?.map(part => part.text || '')
            .join('')
            .trim();

        if (!content) {
            throw new Error('Gemini returned an empty response');
        }

        const roadmap = JSON.parse(content);
        if (
            typeof roadmap.title !== 'string' ||
            typeof roadmap.description !== 'string' ||
            !Array.isArray(roadmap.steps) ||
            roadmap.steps.length === 0 ||
            !roadmap.steps.every(step =>
                step &&
                typeof step.title === 'string' &&
                typeof step.description === 'string'
            )
        ) {
            throw new Error('Gemini returned a roadmap with an invalid format');
        }

        roadmap.steps = roadmap.steps.map(step => ({
            title: step.title,
            description: step.description,
            completed: false
        }));
        return roadmap;
    } finally {
        clearTimeout(timeout);
    }
}

exports.generateRoadmap = async (req, res) => {
    try {
        const { currentSkill, goal, experience } = req.body || {};
        if (
            typeof currentSkill !== 'string' ||
            typeof goal !== 'string' ||
            !currentSkill.trim() ||
            !goal.trim() ||
            currentSkill.length > 100 ||
            goal.length > 100
        ) {
            return res.status(400).json({
                message: 'Current skill and goal are required and must be 100 characters or fewer'
            });
        }

        const apiKey = process.env.GEMINI_API_KEY;
        if (!apiKey) {
            return res.status(503).json({ message: 'AI roadmap generation is not configured on the server' });
        }

        const exp = typeof experience === 'string' && experience.trim()
            ? experience.trim()
            : 'Beginner';
        const roadmap = await generateGeminiRoadmap(currentSkill.trim(), goal.trim(), exp, apiKey);
        return res.json(roadmap);
    } catch (err) {
        console.error('Generate roadmap error:', err.message);
        return res.status(502).json({ message: 'AI roadmap generation failed. Please try again later.' });
    }
};
