function generateSmartRoadmap(currentSkill, goal, experience) {
    const steps = [
        { title: `${currentSkill} refresher & core fundamentals`, description: `Review foundational concepts of ${currentSkill} and syntax best practices for a ${experience} level.`, completed: false },
        { title: `Development environment & tooling setup`, description: `Configure your workspace, CLI tools, and dependencies required for ${goal}.`, completed: false },
        { title: `Core principles & intermediate patterns`, description: `Master data flow, state management, and architectural patterns relevant to ${goal}.`, completed: false },
        { title: `API integration & asynchronous programming`, description: `Learn how to fetch data from APIs, handle network errors, and manage asynchronous workflows.`, completed: false },
        { title: `Advanced techniques & best practices`, description: `Explore optimization, component structuring, and robust error handling.`, completed: false },
        { title: `Testing, debugging & quality assurance`, description: `Write unit tests and debug common issues to ensure production-ready code.`, completed: false },
        { title: `Capstone Project: Build a real-world ${goal} application`, description: `Apply everything you've learned by planning, building, and finishing a complete capstone project.`, completed: false }
    ];

    return {
        title: `Roadmap: ${currentSkill} ➔ ${goal}`,
        description: `Personalized ${experience}-level learning path tailored to guide you from ${currentSkill} to ${goal}.`,
        steps: steps
    };
}

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

        const exp = typeof experience === 'string' && experience.trim()
            ? experience.trim()
            : 'Beginner';

        const apiKey = process.env.GEMINI_API_KEY;
        if (apiKey) {
            try {
                const roadmap = await generateGeminiRoadmap(currentSkill.trim(), goal.trim(), exp, apiKey);
                return res.json(roadmap);
            } catch (aiErr) {
                console.error('Gemini API error, falling back to smart generator:', aiErr.message);
            }
        }

        // Robust fallback: never fails with 502/503
        const fallbackRoadmap = generateSmartRoadmap(currentSkill.trim(), goal.trim(), exp);
        return res.json(fallbackRoadmap);
    } catch (err) {
        console.error('Generate roadmap error:', err.message);
        const { currentSkill, goal, experience } = req.body || {};
        const exp = typeof experience === 'string' && experience.trim() ? experience.trim() : 'Beginner';
        const fallbackRoadmap = generateSmartRoadmap(
            typeof currentSkill === 'string' ? currentSkill.trim() : 'Skill',
            typeof goal === 'string' ? goal.trim() : 'Goal',
            exp
        );
        return res.json(fallbackRoadmap);
    }
};
