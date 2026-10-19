function generateSmartRoadmap(currentSkill, goal, experience) {
    const isDesignOrArt = /design|art|mehandi|drawing|painting|music|cooking|craft|photo|video|yoga|fashion|dance|beauty/i.test(goal + " " + currentSkill);

    let steps = [];
    if (isDesignOrArt) {
        steps = [
            { title: `Foundations & Essential Materials for ${currentSkill}`, description: `Understand basic techniques, material preparation, and sketching principles for ${goal}.`, completed: false },
            { title: `Mastering Precision, Lines & Symmetry`, description: `Practice core hand movements, intricate patterns, and structural layout techniques.`, completed: false },
            { title: `Advanced Composition & Creative Styles`, description: `Explore contemporary styles, shading, depth, and unique artistic variations.`, completed: false },
            { title: `Speed, Flow & Practical Execution`, description: `Build muscle memory, work efficiently under time constraints, and refine your technique.`, completed: false },
            { title: `Portfolio & Masterpiece Project`, description: `Create a professional portfolio or showcase piece applying your ${goal} mastery.`, completed: false }
        ];
    } else {
        steps = [
            { title: `${currentSkill} refresher & core fundamentals`, description: `Review foundational concepts of ${currentSkill} and syntax/best practices for a ${experience} level.`, completed: false },
            { title: `Development environment & tooling setup`, description: `Configure your workspace, CLI tools, and dependencies required for ${goal}.`, completed: false },
            { title: `Core architecture & intermediate patterns`, description: `Master data flow, state management, and structural patterns relevant to ${goal}.`, completed: false },
            { title: `API integration & asynchronous workflows`, description: `Learn how to fetch data from APIs, handle network errors, and manage backend interactions.`, completed: false },
            { title: `Advanced techniques & optimization`, description: `Explore performance tuning, component structuring, and robust error handling.`, completed: false },
            { title: `Testing, debugging & quality assurance`, description: `Write unit tests and debug common issues to ensure production-ready code.`, completed: false },
            { title: `Capstone Project: Build a real-world ${goal} application`, description: `Apply everything you've learned by planning, building, and finishing a complete capstone project.`, completed: false }
        ];
    }

    return {
        title: `Roadmap: ${currentSkill} ➔ ${goal}`,
        description: `Personalized ${experience}-level learning path tailored to guide you from ${currentSkill} to ${goal}.`,
        steps: steps
    };
}

async function generateGeminiRoadmap(currentSkill, goal, experience, apiKey) {
    const prompt = `Create a highly specialized, practical, and personalized learning roadmap for a ${experience} learner who currently knows "${currentSkill}" and wants to master "${goal}". Ensure the roadmap is completely domain-specific (e.g. if it is a design, art, or creative skill like Mehandi Design, tailor it to artistic techniques and practice; if it is technical, tailor it to software engineering). Return a JSON object with a concise "title", a helpful "description", and a "steps" array (between 5 to 7 steps). Each step must have a "title", a useful "description", and "completed": false.`;
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
            const errorBody = await response.text();
            let message = `Gemini API returned HTTP ${response.status}`;
            if (errorBody) {
                try {
                    const parsedError = JSON.parse(errorBody);
                    const detail = parsedError.error?.message;
                    message += detail ? `: ${detail}` : `: ${errorBody.slice(0, 300)}`;
                } catch {
                    message += `: ${errorBody.slice(0, 300)}`;
                }
            }
            throw new Error(message);
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

        // Robust fallback: domain-aware smart generator
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
